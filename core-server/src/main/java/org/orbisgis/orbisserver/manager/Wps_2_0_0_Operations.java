/**
 * OrbisServer is an OSGI web application to expose OGC services.
 *
 * OrbisServer is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * OrbisServer is distributed under LGPL 3 license.
 *
 * Copyright (C) 2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * OrbisServer is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisServer is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * OrbisServer. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisserver.manager;

import net.opengis.ows._2.AcceptVersionsType;
import net.opengis.ows._2.CodeType;
import net.opengis.ows._2.SectionsType;
import net.opengis.wps._2_0.*;
import org.orbisgis.orbisserver.control.utils.ProcessContent;
import org.orbiswps.server.model.JaxbContainer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class managing all the operations implemented by a WPS 2.0.0 server.
 *
 * @author Guillaume MANDE
 */
public class Wps_2_0_0_Operations {

    /**
     * List of all identifier of all the processes.
     */
    private static List<CodeType> codeTypeList;

    /**
     * GetStatus object used to display the response the result of the GetStatus operation.
     */
    private static List<GetStatus> getStatusList = new ArrayList<>();

    /**
     * Return the wpsCapabilitiesType object which is a xml object.
     *
     * @throws JAXBException JAXB Exception.
     * @Return the wpsCapabilitiesType object.
     */
    public static WPSCapabilitiesType getResponseFromGetCapabilities() throws JAXBException {
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        ObjectFactory factory = new ObjectFactory();
        //Creates the getCapabilities
        GetCapabilitiesType getCapabilitiesType = new GetCapabilitiesType();
        GetCapabilitiesType.AcceptLanguages acceptLanguages = new GetCapabilitiesType.AcceptLanguages();
        acceptLanguages.getLanguage().add("*");
        getCapabilitiesType.setAcceptLanguages(acceptLanguages);
        AcceptVersionsType acceptVersionsType = new AcceptVersionsType();
        acceptVersionsType.getVersion().add("2.0.0");
        getCapabilitiesType.setAcceptVersions(acceptVersionsType);
        SectionsType sectionsType = new SectionsType();
        sectionsType.getSection().add("All");
        getCapabilitiesType.setSections(sectionsType);
        //Marshall the DescribeProcess object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(factory.createGetCapabilities(getCapabilitiesType), out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) WpsServerManager.getWpsServer().callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        ByteArrayInputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);
        WPSCapabilitiesType wpsCapabilitiesType = (WPSCapabilitiesType) ((JAXBElement) resultObject).getValue();

        return wpsCapabilitiesType;
    }

    /**
     * Return the xml file corresponding to the DescribeProcess request.
     *
     * @param id Unambiguous identifier of the process that shall be executed.
     * @return a ProcessOfferings object
     * @throws JAXBException JAXB Exception.
     */
    public static Object getResponseFromDescribeProcess(String id) throws JAXBException {
        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        //Creates the DescribeProcess
        DescribeProcess describeProcess = new DescribeProcess();
        describeProcess.setLang("en");
        CodeType codeType = new CodeType();
        codeType.setValue(id);
        describeProcess.getIdentifier().add(codeType);
        //Marshall the DescribeProcess object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(describeProcess, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) WpsServerManager.getWpsServer().callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);
        return resultObject;
    }

    /**
     * Return the xml file corresponding to the Execute request.
     *
     * @param id Unambiguous identifier of the process that shall be executed.
     * @param response Desired response format, i.e. a response document or raw data.
     * @param mode Desired execution mode.
     * @param inputData Map of the inputs provided to this process execution.
     * @param outputData Specification of outputs expected from the process execution, including the desired format and
     * transmission mode for each output.
     * @return a StatusInfo object
     * @throws JAXBException JAXB Exception.
     * @throws IOException IOException.
     */
    public static Object getResponseFromExecute(String id, String response, String mode, Map<String, String> inputData,
                                                Map<String, String> outputData)
            throws JAXBException, IOException {

        getProcessIdList();
        ExecuteRequestType execute = new ExecuteRequestType();

        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            DataInputType dataInputType = new DataInputType();
            Data data = new Data();
            data.getContent().add(entry.getValue());
            data.setEncoding("simple");
            data.setMimeType("text/plain");
            dataInputType.setData(data);
            dataInputType.setId(entry.getKey());
            execute.getInput().add(dataInputType);
        }

        for (Map.Entry<String, String> entry : outputData.entrySet()) {
            OutputDefinitionType outputDefinitionType = new OutputDefinitionType();
            outputDefinitionType.setId(entry.getKey());
            outputDefinitionType.setEncoding("simple");
            outputDefinitionType.setMimeType("text/plain");
            outputDefinitionType.setTransmission(DataTransmissionModeType.REFERENCE);
            execute.getOutput().add(outputDefinitionType);
        }


        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        ObjectFactory factory = new ObjectFactory();
        //Creates the ExecuteRequestType

        CodeType codeType = new CodeType();
        codeType.setValue(id);
        execute.setIdentifier(codeType);
        execute.setResponse(response);
        execute.setMode(mode);

        //Marshall the ExecuteRequestType object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(factory.createExecute(execute), out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) WpsServerManager.getWpsServer().callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.
        Object resultObject = unmarshaller.unmarshal(resultXml);

        StatusInfo statusInfo = (StatusInfo)resultObject;
        GetStatus getStatus = new GetStatus();
        getStatus.setJobID(statusInfo.getJobID());
        getStatusList.add(getStatus);

        return resultObject;  //resultObject is a StatusInfo Object
    }

    /**
     * Return the xml file corresponding to the GetStatus request.
     *
     * @param JobId Unambiguously identifier of a job within a WPS instance.
     * @return a StatusInfo object.
     * @throws JAXBException JAXB Exception.
     * @throws IOException IOException.
     */
    public static Object getResponseFromGetStatus(String JobId) throws JAXBException{

        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        //Get the corresponding GetStatus
        GetStatus finalGetStatus = new GetStatus();
        for(GetStatus getStatus : getStatusList){
            if(getStatus.getJobID().equals(JobId)){
                finalGetStatus = getStatus;
            }
        }
        //Marshall the GetStatus object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(finalGetStatus, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) WpsServerManager.getWpsServer().callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.

        return unmarshaller.unmarshal(resultXml);
    }

    /**
     * Return the xml file corresponding to the GetResult request.
     *
     * @param JobId Unambiguously identifier of a job within a WPS instance.
     * @return a StatusInfo object.
     * @throws JAXBException JAXB Exception.
     * @throws IOException IOException.
     */
    public static Object getResponseFromGetResult(String JobId) throws JAXBException{

        Unmarshaller unmarshaller = JaxbContainer.JAXBCONTEXT.createUnmarshaller();
        Marshaller marshaller = JaxbContainer.JAXBCONTEXT.createMarshaller();
        //Get the corresponding GetStatus
        GetResult getResult = new GetResult();
        getResult.setJobID(JobId);
        //Marshall the GetResult object into an OutputStream
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(getResult, out);
        //Write the OutputStream content into an Input stream before sending it to the wpsService
        InputStream in = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ByteArrayOutputStream xml = (ByteArrayOutputStream) WpsServerManager.getWpsServer().callOperation(in);
        //Get back the result of the DescribeProcess request as a BufferReader
        InputStream resultXml = new ByteArrayInputStream(xml.toByteArray());
        //Unmarshall the result and check that the object is the same as the resource unmashalled xml.

        return unmarshaller.unmarshal(resultXml);
    }

    /**
     * Return the list of jobid.
     * @return the list of jobid.
     */
    public static List<String> getGetStatus(){
        List<String> listId = new ArrayList<>();
        for(GetStatus getStatus : getStatusList){
            listId.add(getStatus.getJobID());
        }
        return listId;
    }

    /**
     * Return the last jobid add to the list.
     * @return a jobid.
     */
    public static String getLastJobId() {
        String JobId = "";
        for (GetStatus getStatus : getStatusList) {
            JobId = getStatus.getJobID();
        }
        return JobId;
    }

    /**
     * Method to get the list containing the id, the html id and the title of the processes
     *
     * @Return The map containing the id, the html id and the title.
     * @throws JAXBException JAXBException.
     */
    public static List<ProcessContent> getProcessIdList() throws JAXBException {
        List<ProcessContent> processeIdList = new ArrayList<>();

        codeTypeList = new ArrayList<>();

        List<ProcessSummaryType> list = getResponseFromGetCapabilities().getContents().getProcessSummary();
        for (ProcessSummaryType processSummaryType : list) {
            processeIdList.add(new ProcessContent(
                    processSummaryType.getIdentifier().getValue().replaceAll("([:/.-])", ""),
                    processSummaryType.getIdentifier().getValue(),
                    processSummaryType.getTitle().get(0).getValue()));
            codeTypeList.add(processSummaryType.getIdentifier());
        }
        return processeIdList;
    }

    /**
     * This method returns the list of identifiers from the CodeType's list.
     *
     * @return List of String.
     * @throws JAXBException JAXBException.
     */
    public static List<String> getCodeTypeList() throws JAXBException {
        getProcessIdList();
        List<String> listId = new ArrayList<String>();

        for(CodeType codeType : codeTypeList){
            listId.add(codeType.getValue());
        }
        return listId;
    }

    /**
     * This method returns the CodeType corresponding to the identifier.
     *
     * @return CodeType object.
     * @throws JAXBException JAXBException.
     */
    public static CodeType getCodeTypeFromId(String id) throws JAXBException{
        CodeType codeTypeFinal = new CodeType();
        for(CodeType codeType : codeTypeList){
            if(codeType.getValue().equals(id)){
                codeTypeFinal = codeType;
            }
        }
        return codeTypeFinal;
    }

}

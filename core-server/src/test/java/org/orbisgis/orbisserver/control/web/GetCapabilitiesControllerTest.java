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

package org.orbisgis.orbisserver.control.web;

import org.junit.Assert;
import org.junit.Test;
import org.wisdom.api.http.Result;
import org.wisdom.api.templates.Template;
import org.wisdom.test.parents.WisdomUnitTest;
import static org.mockito.Mockito.mock;

/**
 * A couple of unit tests.
 */
public class GetCapabilitiesControllerTest extends WisdomUnitTest {
    /**
     * Checks that the GetCapabilitiesController is returning OK.
     */
    @Test
    public void testIndex() throws Exception {
        GetCapabilitiesController controller = new GetCapabilitiesController();
        // Use a mock to simulate the template.
        // You can do this for every service and template your controller is using.
        controller.getCapabilities = mock(Template.class);
        Result result = controller.getCapabilities();

        Assert.assertEquals(result.getStatusCode(), OK);
    }
}

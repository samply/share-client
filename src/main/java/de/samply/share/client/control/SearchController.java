/*
 * Copyright (c) 2017 Medical Informatics Group (MIG),
 * Universitätsklinikum Frankfurt
 *
 * Contact: www.mig-frankfurt.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.share.client.control;

import de.samply.share.common.control.uiquerybuilder.AbstractSearchController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * The only purpose of this is to offer an implementation of the search controller, so that the operator list is found.
 * TODO change this in share common!
 *
 */
@ManagedBean(name = "SearchController")
@ViewScoped
public class SearchController extends AbstractSearchController {
    @Override
    public String getSerializedQuery() {
        return null;
    }

    @Override
    public void setSerializedQuery(String s) {

    }

    @Override
    public String onStoreAndRelease() {
        return null;
    }

    @Override
    public String onSubmit() {
        return null;
    }
}

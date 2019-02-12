package de.samply.share.client.quality.report.chainlinks.instances.view;/*
* Copyright (C) 2017 Medizinische Informatik in der Translationalen Onkologie,
* Deutsches Krebsforschungszentrum in Heidelberg
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
* along with this program; if not, see http://www.gnu.org/licenses.
*
* Additional permission under GNU GPL version 3 section 7:
*
* If you modify this Program, or any covered work, by linking or combining it
* with Jersey (https://jersey.java.net) (or a modified version of that
* library), containing parts covered by the terms of the General Public
* License, version 2.0, the licensors of this Program grant you additional
* permission to convey the resulting work.
*/

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.views.ViewsCreator;
import de.samply.share.model.common.View;

import java.util.List;

public class CreateViews_ChainLink<I extends ChainLinkItem & ViewsContext> extends ChainLink<I> {

    private Model model;
    private ViewsCreator viewsCreator;

    public CreateViews_ChainLink(Model model, ViewsCreator viewsCreator) {

        this.model = model;
        this.viewsCreator = viewsCreator;

    }

    @Override
    protected String getChainLinkId() {
        return "Views Creator";
    }

    @Override
    protected I process(I item) throws ChainLinkException {

        List<View> views = viewsCreator.createViews(model);
        item.setViews(views);

        return item;

    }


}

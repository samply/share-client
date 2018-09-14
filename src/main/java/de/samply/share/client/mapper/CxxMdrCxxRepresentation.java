package main.java.de.samply.share.client.mapper;/*
 * Copyright (C) 2018 Medizinische Informatik in der Translationalen Onkologie,
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

public class CxxMdrCxxRepresentation {

    Integer oid;
    String entitySource = "CENTRAXX";
    Integer creator = 1;
    Integer mdrRepresentationOid;
    String cxxClassName;
    String cxxValueName;

    public Integer getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public String getEntitySource() {
        return entitySource;
    }

    public void setEntitySource(String entitySource) {
        this.entitySource = entitySource;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getMdrRepresentationOid() {
        return mdrRepresentationOid;
    }

    public void setMdrRepresentationOid(Integer mdrRepresentationOid) {
        this.mdrRepresentationOid = mdrRepresentationOid;
    }

    public String getCxxClassName() {
        return cxxClassName;
    }

    public void setCxxClassName(String cxxClassName) {
        this.cxxClassName = cxxClassName;
    }

    public String getCxxValueName() {
        return cxxValueName;
    }

    public void setCxxValueName(String cxxValueName) {
        this.cxxValueName = cxxValueName;
    }

}
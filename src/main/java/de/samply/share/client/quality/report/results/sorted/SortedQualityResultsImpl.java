package de.samply.share.client.quality.report.results.sorted;/*
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

import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.filter.QualityResultsFilter;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.ArrayList;
import java.util.Iterator;


public abstract class SortedQualityResultsImpl extends QualityResultsFilter implements SortedQualityResults  {


    private ArrayList<MdrIdValue> sortedMdrIdValues;

    protected abstract ArrayList<MdrIdValue> sortQualityResults (QualityResults qualityResults);

    public SortedQualityResultsImpl(QualityResults qualityResults) {
        super(qualityResults);
        sortedMdrIdValues = sortQualityResults(qualityResults);
    }

    @Override
    public int getOrdinal(MdrIdDatatype mdrId, String value) {

        MdrIdValue mdrIdValue = new MdrIdValue(mdrId, value);
        return sortedMdrIdValues.indexOf(mdrIdValue);

    }

    @Override
    public MdrIdDatatype getMdrId(int ordinal) {

        MdrIdValue mdrIdValue = sortedMdrIdValues.get(ordinal);
        return (mdrIdValue == null) ? null : mdrIdValue.getMdrId();

    }

    @Override
    public String getValue(int ordinal) {

        MdrIdValue mdrIdValue = sortedMdrIdValues.get(ordinal);
        return (mdrIdValue == null) ? null : mdrIdValue.getValue();

    }

    private class SortedQualityResultsIterator implements Iterator<QualityResult>{

        private Iterator<MdrIdValue> mdrIdValueIterator;

        public SortedQualityResultsIterator() {
            this.mdrIdValueIterator = sortedMdrIdValues.iterator();
        }

        @Override
        public boolean hasNext() {
            return mdrIdValueIterator.hasNext();
        }

        @Override
        public QualityResult next() {

            MdrIdValue next = mdrIdValueIterator.next();
            return qualityResults.getResult(next.getMdrId(), next.getValue());

        }

        @Override
        public void remove() {
            mdrIdValueIterator.remove();
        }
    }

    @Override
    public Iterator<QualityResult> iterator() {
        return new SortedQualityResultsIterator();
    }

}

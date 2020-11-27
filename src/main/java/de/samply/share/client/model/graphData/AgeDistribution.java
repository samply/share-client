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

package de.samply.share.client.model.graphData;

import java.util.TreeMap;
import java.util.Map;

public class AgeDistribution {
    private Map<Integer, Integer> data;

    public Map<Integer, Integer> getData() {
        return data;
    }

    public void setData(Map<Integer, Integer> data) {
        this.data = data;
    }

    public AgeDistribution() {
        data = new TreeMap<>();
    }

    public int getAmountByAge(int age) {
        try {
            return data.get(age);
        } catch (Exception e) {
            return 0;
        }
    }

    public synchronized void incrementCountForAge(int age) {
        addToAge(age, 1);
    }

    public synchronized void addToAge(int age, int amount) {
        data.put(age, getAmountByAge(age) + amount);
    }

    @Override
    public String toString() {
        return "AgeDistribution{" +
                "data=" + data +
                '}';
    }
}

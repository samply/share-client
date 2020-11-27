package de.samply.share.client.quality.report.chainlinks.timer;/*
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

public abstract class ChainLinkTimerImpl implements ChainLinkTimer {

    private long maxTimeToWaitInMillis;

    protected abstract long getTimeToSleepInMillis (int attempt);

    @Override
    public void mySleep(int attempt) {

        try {
            Thread.sleep (getTimeToSleepInMillis(attempt));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void myWait() {

        try {
            wait(maxTimeToWaitInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void myNotify() {
        notifyAll();
    }

    public void setMaxTimeToWaitInMillis(long maxTimeToWaitInMillis) {
        this.maxTimeToWaitInMillis = maxTimeToWaitInMillis;
    }

}

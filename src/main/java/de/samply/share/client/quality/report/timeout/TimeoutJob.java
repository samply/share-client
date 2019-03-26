package de.samply.share.client.quality.report.timeout;/*
 * Copyright (C) 2019 Medizinische Informatik in der Translationalen Onkologie,
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

import de.samply.share.client.quality.report.chainlinks.finalizer.ChainLinkFinalizer;
import de.samply.share.client.quality.report.chainlinks.finalizer.ChainLinkFinalizerListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimeoutJob implements Runnable, ChainLinkFinalizerListener {

    private ChainLinkFinalizer chainLinkFinalizer;
    private long timeout;
    private Logger logger = LogManager.getLogger(TimeoutJob.class);
    private boolean isTimeoutReached = false;
    private boolean isChainAlreadyFinished = false;


    public TimeoutJob(ChainLinkFinalizer chainLinkFinalizer, long timeout) {

        this.chainLinkFinalizer = chainLinkFinalizer;
        chainLinkFinalizer.addChainLinkFinalizerListener(this);

        this.timeout = timeout;
        Thread thread = new Thread(this);
        thread.start();

    }

    @Override
    public void run() {
        myWait();
    }

    private void myWait(){
        try {

            myWait_WithoutExceptionManagement();

        } catch (InterruptedException e) {

            logger.info(e);
            finalizeChain();

        }
    }

    private synchronized void myWait_WithoutExceptionManagement () throws InterruptedException {

        wait(timeout);

        if (!isChainAlreadyFinished) {

            isTimeoutReached = true;
            logTimeoutReached();
            finalizeChain();
            chainLinkFinalizer.setAtLeastOneTimeoutReached();

        }
    }

    private void logTimeoutReached(){
        logger.info("Timeout was reached by QB-Generator: ");
    }

    private void finalizeChain (){

        if (!isChainAlreadyFinished){

            chainLinkFinalizer.finalizeAll();
            isChainAlreadyFinished = true;

        }

    }

    public synchronized void setChainIsAlreadyFinished(){

        isChainAlreadyFinished = true;
        notifyAll();

    }

    public boolean isTimeoutReached(){
        return isTimeoutReached;
    }


    @Override
    public void notifyIsFinished() {
        setChainIsAlreadyFinished();
    }

}

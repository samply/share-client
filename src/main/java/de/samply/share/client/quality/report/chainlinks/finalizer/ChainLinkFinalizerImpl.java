package de.samply.share.client.quality.report.chainlinks.finalizer;/*
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

import java.util.ArrayList;
import java.util.List;

public class ChainLinkFinalizerImpl implements ChainLinkFinalizer{

    private List<ChainLink> chainLinks = new ArrayList<>();
    private List<ChainLinkFinalizerListener> chainLinkFinalizerListeners = new ArrayList<>();
    private boolean isTimeoutReachedInAnyChainLinkFinalizerListener = false;

    @Override
    public synchronized void addChainLink (ChainLink chainLink){
        chainLinks.add(chainLink);
    }

    @Override
    public synchronized void finalizeAll(){

        List<ChainLink> chainLinks = new ArrayList<>();
        chainLinks.addAll(this.chainLinks);

        for (ChainLink chainLink : chainLinks){
            chainLink.finalizeChainLink();
        }

        notifyIsFinalizedToAllListeners();

    }

    @Override
    public synchronized void setChainLinkAsFinalized(ChainLink chainLink){

        chainLinks.remove(chainLink);
        notifyIsFinalizedToAllListenersIfNoMoreChainLinks();

    }

    @Override
    public void addChainLinkFinalizerListener(ChainLinkFinalizerListener chainLinkFinalizerListener) {
        chainLinkFinalizerListeners.add(chainLinkFinalizerListener);
    }

    @Override
    public boolean isTimeoutReachedInAnyChainLinkFinalizerListener () {

        boolean isTimeOutReached = false;
        for (ChainLinkFinalizerListener chainLinkFinalizerListener : chainLinkFinalizerListeners){
            if (chainLinkFinalizerListener.isTimeoutReached()){
                isTimeOutReached = true;
            }
        }

        if (chainLinkFinalizerListeners.size() > 0){
            isTimeoutReachedInAnyChainLinkFinalizerListener = isTimeOutReached;
        }

        return isTimeoutReachedInAnyChainLinkFinalizerListener;

    }

    @Override
    public void setAtLeastOneTimeoutReached() {
        isTimeoutReachedInAnyChainLinkFinalizerListener = true;
    }

    private void notifyIsFinalizedToAllListenersIfNoMoreChainLinks(){

        if (chainLinks.size() == 0){
            notifyIsFinalizedToAllListeners();
        }

    }

    private void notifyIsFinalizedToAllListeners(){

        List<ChainLinkFinalizerListener> chainLinkFinalizerListenerList = new ArrayList<>();
        chainLinkFinalizerListenerList.addAll(chainLinkFinalizerListeners);

        for (ChainLinkFinalizerListener chainLinkFinalizerListener : chainLinkFinalizerListenerList){

            chainLinkFinalizerListener.notifyIsFinished();
            chainLinkFinalizerListeners.remove(chainLinkFinalizerListener);

        }

    }

}

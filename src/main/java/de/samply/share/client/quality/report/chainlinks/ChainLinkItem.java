package de.samply.share.client.quality.report.chainlinks;/*
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


public class ChainLinkItem implements Cloneable {


    private int attempt = 0;
    private long elapsedNanoTime = 0;

    protected boolean isToBeReused;
    protected boolean isToBeRepeated;
    protected boolean isToBeForwarded;
    protected boolean isAlreadyUsed = false;

    private ChainLinkError chainLinkError;



    public ChainLinkItem() {
        resetConfigurationValues();
    }

    public boolean isToBeRepeated() {
        return isToBeRepeated;
    }

    public boolean isToBeReused(){
        return isToBeReused;
    }

    public void setToBeRepeated() {
        isToBeRepeated = true;
    }

    public void setToBeReused() {
        isToBeReused = true;
    }

    public boolean isToBeForwarded() {
        return isToBeForwarded;
    }

    public ChainLinkError getChainLinkError() {
        return chainLinkError;
    }

    public void setChainLinkError(ChainLinkError chainLinkError) {
        this.chainLinkError = chainLinkError;
    }

    public void setNotToBeForwarded() {
        isToBeForwarded = false;
    }

    public int getAttempt() {
        return attempt;
    }

    public void incrementAttempt() {
        this.attempt ++;
    }

    public void resetAttempt(){

        attempt = 0;
        elapsedNanoTime = 0;

    }

    public void addElapsedNanoTime (long elapsedNanoTime){

        this.elapsedNanoTime += elapsedNanoTime;

    }

    public long getElapsedNanoTime(){
        return elapsedNanoTime;
    }

    public boolean isAlreadyUsed() {
        return isAlreadyUsed;
    }

    public void setAlreadyUsed(){
        isAlreadyUsed = true;
    }

    public ChainLinkItem clone(){

        try {

            return cloneAndResetOperationalValues();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ChainLinkItem cloneAndResetOperationalValues() throws CloneNotSupportedException {

        ChainLinkItem chainLinkContext = (ChainLinkItem) super.clone();
        chainLinkContext.resetConfigurationValues();
        chainLinkContext.resetAttempt();
        isAlreadyUsed = false;

        return chainLinkContext;

    }

    public void resetConfigurationValues(){


        isToBeReused = false;
        isToBeRepeated = false;
        isToBeForwarded = true;

        chainLinkError = null;

    }

}

/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.io.file.util;

import org.wso2.carbon.messaging.ServerConnector;
import org.wso2.carbon.transport.file.connector.server.FileServerConnector;

import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

/**
 * Class for keep the configurations of a file source instance.
 * */
public class FileSourceConfiguration {

    private boolean isTailingEnabled;
    private String mode;
    private String beginRegex = null;
    private String endRegex = null;
    private String filePointer = "0";
    private String filePollingInterval = null;

    private FileServerConnector fileServerConnector;
    private ServerConnector fileSystemServerConnector;
    private String tailedFileURI = null;
    private ExecutorService executorService = null;
    private String[] requiredProperties = null;
    private StringBuilder tailingRegexStringBuilder = null;
    private Pattern pattern;

    public FileSourceConfiguration() {
        tailingRegexStringBuilder = new StringBuilder();
    }

    public String getBeginRegex() {
        return beginRegex;
    }

    public void setBeginRegex(String beginRegex) {
        this.beginRegex = beginRegex;
    }

    public String getEndRegex() {
        return endRegex;
    }

    public void setEndRegex(String endRegex) {
        this.endRegex = endRegex;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isTailingEnabled() {
        return isTailingEnabled;
    }

    public void setTailingEnabled(boolean tailingEnabled) {
        isTailingEnabled = tailingEnabled;
    }

    public String getFilePointer() {
        return filePointer;
    }

    public void setFilePointer(String filePointer) {
        this.filePointer = filePointer;
    }

    public void updateFilePointer(int valueToAdd) {
        long filePointer = Long.parseLong(this.filePointer);
        filePointer += valueToAdd;
        this.filePointer = Long.toString(filePointer);
    }

    public FileServerConnector getFileServerConnector() {
        return fileServerConnector;
    }

    public void setFileServerConnector(FileServerConnector fileServerConnector) {
        this.fileServerConnector = fileServerConnector;
    }

    public ServerConnector getFileSystemServerConnector() {
        return fileSystemServerConnector;
    }

    public void setFileSystemServerConnector(ServerConnector fileSystemServerConnector) {
        this.fileSystemServerConnector = fileSystemServerConnector;
    }

    public String getTailedFileURI() {
        return tailedFileURI;
    }

    public void setTailedFileURI(String tailedFileURI) {
        this.tailedFileURI = tailedFileURI;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public String[] getRequiredProperties() {
        return requiredProperties.clone();
    }

    public void setRequiredProperties(String[] requiredProperties) {
        this.requiredProperties = requiredProperties.clone();
    }

    public String getFilePollingInterval() {
        return filePollingInterval;
    }

    public void setFilePollingInterval(String filePollingInterval) {
        this.filePollingInterval = filePollingInterval;
    }

    public StringBuilder getTailingRegexStringBuilder() {
        return tailingRegexStringBuilder;
    }

    public void updateTailingRegexStringBuilder(StringBuilder tailingRegexStringBuilder) {
        this.tailingRegexStringBuilder = tailingRegexStringBuilder;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}

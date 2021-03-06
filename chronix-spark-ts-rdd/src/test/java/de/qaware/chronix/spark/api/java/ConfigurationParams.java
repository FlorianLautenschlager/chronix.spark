/*
 * Copyright (C) 2016 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.qaware.chronix.spark.api.java;

import de.qaware.chronix.spark.api.java.helpers.ChronixSolrCloudStorageMock;
import de.qaware.chronix.storage.solr.ChronixSolrCloudStorage;
import de.qaware.chronix.timeseries.MetricTimeSeries;

/**
 * Some common configuration for the test suite.
 */
public class ConfigurationParams {
    // *** cloud computing case
    // public static final String CHRONIX_COLLECTION = "ekgdata";
    public static final String CHRONIX_COLLECTION = "chronix";
    public static final String ZK_HOST = "localhost:9983";
    // *** cloud computing case
    // public static final String ZK_HOST = "192.168.1.100:2181";
    public static final String SPARK_MASTER = "local[4]";
    public static final String APP_NAME = "Spark Chronix";

    public static final String SOLR_REFERNCE_QUERY = "metric:\"java.lang:type=Memory/HeapMemoryUsage/used\"";
    public static final String DEFAULT_TESTDATA_FILE = "timeseries-testdata.bin";

    public static final ChronixSolrCloudStorage<MetricTimeSeries> STORAGE = new ChronixSolrCloudStorageMock();
}

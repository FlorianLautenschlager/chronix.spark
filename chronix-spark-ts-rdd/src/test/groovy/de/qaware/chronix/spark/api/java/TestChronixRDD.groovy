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
package de.qaware.chronix.spark.api.java

import de.qaware.chronix.spark.api.java.timeseries.MetricDimensions
import de.qaware.chronix.timeseries.MetricTimeSeries
import org.apache.solr.client.solrj.SolrQuery
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLContext
import spock.lang.Shared
import spock.lang.Specification

import static org.junit.Assert.assertTrue

class TestChronixRDD extends Specification {

    @Shared
    SparkConf conf
    @Shared
    JavaSparkContext sc
    @Shared
    ChronixSparkContext csc
    @Shared
    SolrQuery query
    @Shared
    ChronixRDD rdd

    def setup() {
        conf = new SparkConf().setMaster(ConfigurationParams.SPARK_MASTER).setAppName(ConfigurationParams.APP_NAME)
        conf.set("spark.driver.allowMultipleContexts", "true")
        sc = new JavaSparkContext(conf)
        csc = new ChronixSparkContext(sc);
        query = new SolrQuery(ConfigurationParams.SOLR_REFERNCE_QUERY);
        rdd = csc.queryChronixChunks(query, ConfigurationParams.ZK_HOST, ConfigurationParams.CHRONIX_COLLECTION, ConfigurationParams.STORAGE);
    }

    def "test iterator"() {
        when:
        Iterator<MetricTimeSeries> it = rdd.iterator();
        int i = 0;
        while (it.hasNext()) {
            it.next();
            i++;
        }
        then:
        i > 0
        cleanup:
        sc.close()
    }

    def "test mean"() {
        when:
        long start = System.currentTimeMillis();
        double mean = rdd.mean();
        long count = rdd.countObservations()
        long stop = System.currentTimeMillis();
        println "Mean: " + mean
        println "   - execution time: " + (stop - start)
        println "   - relevant observations: " + count
        then:
        mean > 0.0f
        cleanup:
        sc.close()
    }

    def "test approx. mean"() {
        when:
        long start = System.currentTimeMillis();
        double mean = rdd.approxMean()
        long stop = System.currentTimeMillis();
        println "Approx. mean: " + mean
        println "   - execution time: " + (stop - start)
        then:
        mean > 0.0f
        cleanup:
        sc.close()
    }

    def "test scalar actions"() {
        when:
        long count = rdd.countObservations()
        double max = rdd.max()
        double min = rdd.min()
        println "Count: " + count
        println "Max: " + max
        println "Min: " + min
        then:
        count > 0
        cleanup:
        sc.close()
    }

    def "test data frame"() {
        given:
        SQLContext sqlContext = new SQLContext(sc);
        when:
        DataFrame df = rdd.toDataFrame(sqlContext);
        df.show();
        then:
        //Assert that all columns are available
        List<String> columns = Arrays.asList(df.columns());
        assertTrue(columns.contains("timestamp"));
        assertTrue(columns.contains("value"));
        for (MetricDimensions dim : MetricDimensions.values()) {
            assertTrue(columns.contains(dim.getId()));
        }
        //Assert that DataFrame is not empty
        assertTrue(df.count() > 0);
        cleanup:
        sc.close()
    }

}
/*
 * Copyright (c) Microsoft Corporation
 * <p/>
 * All rights reserved.
 * <p/>
 * MIT License
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package com.microsoft.azure.hdinsight.spark.run.configuration

import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Attribute
import com.microsoft.azure.hdinsight.sdk.rest.azure.synapse.models.NodeSize
import com.microsoft.azure.hdinsight.sdk.rest.azure.synapse.models.NodeSize.*
import com.microsoft.azure.hdinsight.spark.common.SparkSubmissionParameter
import com.microsoft.azure.hdinsight.spark.common.SparkSubmitModel
import com.microsoft.azure.hdinsight.spark.run.action.SparkApplicationType
import com.microsoft.azuretools.utils.Pair
import java.util.stream.Stream

data class ExecutorSize(val executorCores: Int = 0, val executorMemory: String = "0G")

class ArcadiaSparkSubmitModel(project: Project) : SparkSubmitModel(project) {
    @Attribute("livy_uri")
    var livyUri: String? = null
    @Attribute("spark_workspace")
    var sparkWorkspace: String? = null
    @Attribute("spark_compute")
    var sparkCompute: String? = null
    @Attribute("tenant_id")
    var tenantId: String? = null
    @Attribute("spark_app_type")
    var sparkApplicationType: SparkApplicationType = SparkApplicationType.None

    var nodeSize: NodeSize? = NONE
    var maxNodeCount: Int = 0

    private val nodeSizeToExecutorSize: Map<NodeSize, ExecutorSize> = hashMapOf(
            NONE to ExecutorSize(0, "0G"),
            SMALL to ExecutorSize(4, "28G"),
            MEDIUM to ExecutorSize(8, "56G"),
            LARGE to ExecutorSize(16, "112G"))

    override fun getDefaultParameters(): Stream<Pair<String, out Any>> {
        return listOf(
                Pair(SparkSubmissionParameter.DriverMemory, nodeSizeToExecutorSize.getOrDefault(nodeSize,
                        ExecutorSize()).executorMemory),
                Pair(SparkSubmissionParameter.DriverCores, nodeSizeToExecutorSize.getOrDefault(nodeSize,
                        ExecutorSize()).executorCores),
                Pair(SparkSubmissionParameter.ExecutorMemory, nodeSizeToExecutorSize.getOrDefault(nodeSize,
                        ExecutorSize()).executorMemory),
                Pair(SparkSubmissionParameter.ExecutorCores, nodeSizeToExecutorSize.getOrDefault(nodeSize,
                        ExecutorSize()).executorCores),
                Pair(SparkSubmissionParameter.NumExecutors, maxNodeCount - 1)).stream()
    }

    override fun getSparkClusterTypeDisplayName(): String = "Apache Spark Pool for Azure Synapse"
}
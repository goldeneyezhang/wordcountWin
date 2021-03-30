import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS, LogisticRegressionWithSGD}
import org.apache.spark.mllib.regression.GeneralizedLinearAlgorithm
import org.apache.spark.mllib.evaluation.MulticlassMetrics
/**
 * @author yibozhang@ctrip.com
 * @create: 2021-03-29 16:46
 * @description
 * */
object Iris {
  def main(args: Array[String]): Unit ={
    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("iris")
      .config("spark.sql.warehouse.dir", ".")
      .getOrCreate()
    val df = spark.read.format("csv").option("sep",",").option("inferSchema","true").option("header", "true") .load("iris.data")
    val indexer = new StringIndexer().setInputCol("Species").setOutputCol("categoryIndex")
    val model = indexer.fit(df)
    val indexed = model.transform(df)
    //得到特征值和便签的索引

    val assembler = new VectorAssembler()
      .setInputCols(Array("Sepal_Length", "Sepal_Width", "Petal_Length","Petal_Width"))
      .setOutputCol("features")
    val output = assembler.transform(indexed)
    //测试集与训练集分开
    val splits = output.randomSplit(Array(0.8,0.2),seed = 111)
    val trainingData = splits(0).cache
    val testData = splits(1).cache

    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8).setLabelCol("categoryIndex")

    // 根据设定的模型参数与training data拟合训练得到模型
    val lrModel = lr.fit(trainingData)
   
  }
}

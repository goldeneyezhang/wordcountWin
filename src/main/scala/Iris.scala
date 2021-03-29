import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.mllib.linalg.{Vector,Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
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

    val features = List("Sepal.Length", "Sepal.Width", "Petal.Length", "Petal.Width").map(indexed.columns.indexOf(_))
    val targetInd = indexed.columns.indexOf("categoryIndex")

    val labeledPointIris = indexed.rdd.map(r => LabeledPoint(r.getDouble(targetInd),Vectors.dense(features.map(r.getDouble(_)).toArray)))
    labeledPointIris.foreach(println)
  }
}

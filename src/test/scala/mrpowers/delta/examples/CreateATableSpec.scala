package mrpowers.delta.examples

import org.scalatest.FunSpec
import com.github.mrpowers.spark.daria.sql.SparkSessionExt._
import com.github.mrpowers.spark.daria.utils.NioUtils
import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import org.apache.spark.sql.{Row, SaveMode}
import org.apache.spark.sql.types._

class CreateATableSpec extends FunSpec with SparkSessionTestWrapper with DataFrameComparer {

  it("creates a Delta table") {

    val path: String = new java.io.File("./tmp/delta-table/").getCanonicalPath

    val data = spark.range(0, 5)

    data
      .write
      .format("delta")
      .mode("overwrite")
      .save(path)

    val df = spark
      .read
      .format("delta")
      .load(path)

    val expectedDF = spark.createDF(
      List(0L, 1L, 2L, 3L, 4L),
      List(("id", LongType, true))
    )

    assertSmallDataFrameEquality(df, expectedDF)

    NioUtils.removeAll(path)

  }

  it("creates a table with saveAsTable") {
    val whatever = os.pwd/"spark-warehouse"/"whatever"
    if (os.exists(whatever)) os.remove.all(whatever)
    val df = spark.range(3)
    df.write.format("delta").saveAsTable("whatever")
    if (os.exists(whatever)) os.remove.all(whatever)
  }

  it("creates an empty table with saveAsTable") {
    val b = os.pwd/"spark-warehouse"/"b"
    if (os.exists(b)) os.remove.all(b)
    val df = spark.createDataFrame(
      spark.sparkContext.emptyRDD[Row],
      new StructType().add("name", StringType))
    df.write.format("delta").mode(SaveMode.Overwrite).saveAsTable("b")
    if (os.exists(b)) os.remove.all(b)
  }

}

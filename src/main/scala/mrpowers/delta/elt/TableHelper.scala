package mrpowers.delta.elt

import org.apache.spark.sql.{Column, SparkSession}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}

object TableHelper {

  val decimalType = DataTypes.createDecimalType(32, 9)

  def createSinkTable(spark: SparkSession, tableName: String, schema: StructType, partitionColumns: List[String], rootPath: String): Unit = synchronized {
    val fields = getTableFields(schema)
    val partitions = partitionColumns.mkString("(", ",", ")")
    var location = rootPath.replace("C:", "").replace("\\", "/")
    location = "'" + location + "/" + tableName + "/sink'"

    spark.sql(
      s"""CREATE TABLE IF NOT EXISTS $tableName $fields
        |  USING DELTA
        |    PARTITIONED BY $partitions
        |  LOCATION  $location""".stripMargin)
  }

  private def getTableFields(schema: StructType) = {
    schema.fields
      .map(createField)
      .toList.mkString("(", ",\n ", ")")
  }

  private def createField(field: StructField) = {
    field.name + " " + field.dataType.sql + " " + (if (!field.nullable) "NOT NULL" else "")
  }

  def createInvalidRecordsTable(rootPath: String): Unit = synchronized {
//DPDPDP
  }

  def createValidConditionExpr(schema: StructType): Column = {
    var column = col("dummy")
    var first = true

    for (field <- schema.fields)
      if (!field.nullable)

        if (first) {
          column = createColumn(field)
          first = false
        } else
          column = column.and(createColumn(field))

    column
  }

  private def createColumn(field: StructField) = {
    col("\"" + field.name + "\"").isNotNull
  }
}

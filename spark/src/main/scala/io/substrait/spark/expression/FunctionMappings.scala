/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.substrait.spark.expression

import org.apache.spark.sql.catalyst.analysis.FunctionRegistryBase
import org.apache.spark.sql.catalyst.expressions._
import org.apache.spark.sql.catalyst.expressions.aggregate._

import scala.reflect.ClassTag

case class Sig(expClass: Class[_], name: String, builder: Seq[Expression] => Expression) {
  def makeCall(args: Seq[Expression]): Expression =
    builder(args)
}

class FunctionMappings {

  private def s[T <: Expression: ClassTag](name: String): Sig = {
    val builder = FunctionRegistryBase.build[T](name, None)._2
    Sig(scala.reflect.classTag[T].runtimeClass, name, builder)
  }

  val SCALAR_SIGS: Seq[Sig] = Seq(
    s[Add]("add"),
    s[Subtract]("subtract"),
    s[Multiply]("multiply"),
    s[Divide]("divide"),
    s[Abs]("abs"),
    s[Remainder]("modulus"),
    s[Round]("round"),
    s[Floor]("floor"),
    s[Ceil]("ceil"),
    s[Pow]("power"),
    s[Exp]("exp"),
    s[Sqrt]("sqrt"),
    s[Sin]("sin"),
    s[Cos]("cos"),
    s[Tan]("tan"),
    s[Asin]("asin"),
    s[Acos]("acos"),
    s[Atan]("atan"),
    s[Atan2]("atan2"),
    s[Sinh]("sinh"),
    s[Cosh]("cosh"),
    s[Tanh]("tanh"),
    s[Asinh]("asinh"),
    s[Acosh]("acosh"),
    s[Atanh]("atanh"),
    s[Log]("ln"),
    s[Log10]("log10"),
    s[And]("and"),
    s[Or]("or"),
    s[Not]("not"),
    s[LessThan]("lt"),
    s[LessThanOrEqual]("lte"),
    s[GreaterThan]("gt"),
    s[GreaterThanOrEqual]("gte"),
    s[EqualTo]("equal"),
    s[EqualNullSafe]("is_not_distinct_from"),
    s[IsNull]("is_null"),
    s[IsNotNull]("is_not_null"),
    s[EndsWith]("ends_with"),
    s[Like]("like"),
    s[Contains]("contains"),
    s[StartsWith]("starts_with"),
    s[Substring]("substring"),
    s[Upper]("upper"),
    s[Lower]("lower"),
    s[Concat]("concat"),
    s[Coalesce]("coalesce"),
    s[Year]("year"),
    s[ShiftRight]("shift_right"),
    s[BitwiseAnd]("bitwise_and"),
    s[BitwiseOr]("bitwise_or"),
    s[BitwiseXor]("bitwise_xor"),

    // internal
    s[MakeDecimal]("make_decimal"),
    s[UnscaledValue]("unscaled")
  )

  val AGGREGATE_SIGS: Seq[Sig] = Seq(
    s[Sum]("sum"),
    s[Average]("avg"),
    s[Count]("count"),
    s[Min]("min"),
    s[Max]("max"),
    s[First]("any_value"),
    s[HyperLogLogPlusPlus]("approx_count_distinct"),
    s[StddevSamp]("std_dev")
  )

  val WINDOW_SIGS: Seq[Sig] = Seq(
    s[RowNumber]("row_number"),
    s[Rank]("rank"),
    s[DenseRank]("dense_rank"),
    s[PercentRank]("percent_rank"),
    s[CumeDist]("cume_dist"),
    s[NTile]("ntile"),
    s[Lead]("lead"),
    s[Lag]("lag"),
    s[NthValue]("nth_value")
  )

  lazy val scalar_functions_map: Map[Class[_], Sig] = SCALAR_SIGS.map(s => (s.expClass, s)).toMap
  lazy val aggregate_functions_map: Map[Class[_], Sig] =
    AGGREGATE_SIGS.map(s => (s.expClass, s)).toMap
  lazy val window_functions_map: Map[Class[_], Sig] = WINDOW_SIGS.map(s => (s.expClass, s)).toMap
}

object FunctionMappings extends FunctionMappings

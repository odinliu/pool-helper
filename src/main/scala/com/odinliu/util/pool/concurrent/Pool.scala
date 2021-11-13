/*
 * Copyright 2021 LIU YIDING<odinushuaia@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.odinliu.util.pool.concurrent

import scala.concurrent.Future

trait Pool[A] {
  def reserve(): Future[A]
}

trait ReturnablePool[A] extends Pool[A] {
  def release(a: A): Unit
}

trait PoolHelper {
  implicit val executor: scala.concurrent.ExecutionContext

  def borrow[T, U](pool: ReturnablePool[T])(
    f: T => U,
    onFail: Throwable => Unit = _ => ()): Future[Either[Unit, U]] = {
    pool
      .reserve()
      .map(res => {
        try {
          Right(f(res))
        } catch {
          case t: Throwable => Left(onFail(t))
        } finally {
          pool.release(res)
        }
      })
  }

  def ration[T, U](pool: TimedLimitsPool[T])(
    f: T => U,
    onFail: Throwable => Unit = _ => ()): Future[Either[Unit, U]] = {
    pool
      .reserve()
      .map(res => {
        try {
          Right(f(res))
        } catch {
          case t: Throwable => Left(onFail(t))
        }
      })
  }

}

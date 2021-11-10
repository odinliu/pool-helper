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
package com.odinliu.util.pool.resource

trait AutoReleaseHelper {
  def using[T <: AutoCloseable, U](resource: T)(f: T => U): U = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }

  def autoClose[T <: AutoCloseable, U](res: T)(f: T => U, onFail: Throwable => Unit): Either[Unit, U] = {
    try {
      Right(f(res))
    } catch {
      case t: Throwable => Left(onFail(t))
    } finally {
      res.close()
    }
  }
}

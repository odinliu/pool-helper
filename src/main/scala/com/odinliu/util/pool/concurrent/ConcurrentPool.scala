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

import java.util.concurrent.ConcurrentLinkedQueue

import scala.concurrent.{Future, Promise}

abstract class ConcurrentPool[A](numItems: Int)
  extends ReturnablePool[A] {
  private val requests = new ConcurrentLinkedQueue[Promise[A]]()
  private val items = new ConcurrentLinkedQueue[Future[A]]()

  0.until(numItems).foreach(_ => items.add(makeItem()))

  def capacity(): Int = items.size()
  def delaySize(): Int = requests.size()

  override def reserve(): Future[A] = synchronized {
    val item = items.poll()
    if (item == null) {
      val p = Promise[A]
      requests.add(p)
      p.future
    } else {
      item
    }
  }

  override def release(a: A): Unit = synchronized {
    items.add(Future.successful(a))
    (requests.poll(), items.poll()) match {
      case (null, null) =>
      case (req, null) if req != null =>
        requests.add(req)
      case (null, item) if item != null =>
        items.add(item)
      case (req, item) if req != null && item != null =>
        req.completeWith(item)
    }
  }

  protected def makeItem(): Future[A]
}

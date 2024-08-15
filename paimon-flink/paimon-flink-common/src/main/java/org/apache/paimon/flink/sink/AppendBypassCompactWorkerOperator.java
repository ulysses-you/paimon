/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.flink.sink;

import org.apache.paimon.append.UnawareAppendCompactionTask;
import org.apache.paimon.table.FileStoreTable;

import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.types.Either;

/** A {@link AppendCompactWorkerOperator} to bypass Committable inputs. */
public class AppendBypassCompactWorkerOperator
        extends AppendCompactWorkerOperator<Either<Committable, UnawareAppendCompactionTask>> {

    public AppendBypassCompactWorkerOperator(FileStoreTable table, String commitUser) {
        super(table, commitUser);
    }

    @Override
    public void open() throws Exception {
        super.open();
    }

    @Override
    public void processElement(
            StreamRecord<Either<Committable, UnawareAppendCompactionTask>> element)
            throws Exception {
        if (element.getValue().isLeft()) {
            output.collect(new StreamRecord<>(element.getValue().left()));
        } else {
            unawareBucketCompactor.processElement(element.getValue().right());
        }
    }
}

/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @author max
 */
package com.intellij.util.messages;

import com.intellij.util.messages.impl.MessageBusImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class MessageBusFactory {

  private static final AtomicReference<Impl> ourImpl = new AtomicReference<Impl>(new Impl() {
    @NotNull
    @Override
    public MessageBus newMessageBus(@NotNull Object owner) {
      return new MessageBusImpl.RootBus(owner);
    }

    @NotNull
    @Override
    public MessageBus newMessageBus(@NotNull Object owner, @Nullable MessageBus parentBus) {
      return parentBus == null ? newMessageBus(owner) : new MessageBusImpl(owner, parentBus);
    }
  });

  private MessageBusFactory() {}

  public static MessageBus newMessageBus(@NotNull Object owner) {
    return ourImpl.get().newMessageBus(owner);
  }

  public static MessageBus newMessageBus(@NotNull Object owner, @Nullable MessageBus parentBus) {
    return ourImpl.get().newMessageBus(owner, parentBus);
  }

  public static void setImpl(@NotNull Impl impl) {
    ourImpl.set(impl);
  }

  public interface Impl {

    @NotNull
    MessageBus newMessageBus(@NotNull Object owner);

    @NotNull
    MessageBus newMessageBus(@NotNull Object owner, @Nullable MessageBus parentBus);
  }
}
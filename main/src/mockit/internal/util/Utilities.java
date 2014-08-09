/*
 * Copyright (c) 2006-2014 Rogério Liesenfeld
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.util;

import java.lang.reflect.*;
import java.util.*;

import mockit.internal.state.*;

import org.jetbrains.annotations.*;

/**
 * Miscellaneous utility constants and methods.
 */
public final class Utilities
{
   @NotNull public static final Object[] NO_ARGS = {};

   public static final boolean JAVA6;
   public static final boolean JAVA7;
   public static final boolean JAVA8;
   public static final boolean HOTSPOT_VM;
   static {
      HOTSPOT_VM = System.getProperty("java.vm.name").contains("HotSpot");
      String javaVersion = System.getProperty("java.specification.version");
      JAVA8 = "1.8".equals(javaVersion);
      JAVA7 = "1.7".equals(javaVersion);
      JAVA6 = "1.6".equals(javaVersion);
   }

   private Utilities() {}

   public static void ensureThatMemberIsAccessible(@NotNull AccessibleObject classMember)
   {
      if (!classMember.isAccessible()) {
         classMember.setAccessible(true);
      }
   }

   public static void ensureThatClassIsInitialized(@NotNull Class<?> aClass)
   {
      ExecutingTest executingTest = TestRun.getExecutingTest();
      boolean previousFlag = executingTest.setShouldIgnoreMockingCallbacks(true);

      try {
         Class.forName(aClass.getName(), true, aClass.getClassLoader());
      }
      catch (ClassNotFoundException ignore) {}
      catch (LinkageError e) {
         StackTrace.filterStackTrace(e);
         e.printStackTrace();
      }
      finally {
         executingTest.setShouldIgnoreMockingCallbacks(previousFlag);
      }
   }

   @NotNull
   public static Class<?> getClassType(@NotNull Type declaredType)
   {
      while (true) {
         if (declaredType instanceof Class<?>) {
            return (Class<?>) declaredType;
         }

         if (declaredType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) declaredType).getRawType();
         }

         if (declaredType instanceof TypeVariable) {
            declaredType = ((TypeVariable<?>) declaredType).getBounds()[0];
            continue;
         }

         throw new IllegalArgumentException("Type of unexpected kind: " + declaredType);
      }
   }

   public static boolean containsReference(@NotNull List<?> references, @Nullable Object toBeFound)
   {
      return indexOfReference(references, toBeFound) >= 0;
   }

   public static int indexOfReference(@NotNull List<?> references, @Nullable Object toBeFound)
   {
      for (int i = 0, n = references.size(); i < n; i++) {
         if (references.get(i) == toBeFound) {
            return i;
         }
      }

      return -1;
   }
}

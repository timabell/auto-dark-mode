/*
 * MIT License
 *
 * Copyright (c) 2020-2025 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.weisj.darkmode.platform;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.system.CpuArch;
import com.intellij.util.system.OS;

@SuppressWarnings("java:S115")
public final class LibraryUtil {

    private LibraryUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final boolean isX86 = CpuArch.isIntel32();
    public static final boolean isX64 = CpuArch.isIntel64();
    public static final boolean isWin10OrNewer = OS.CURRENT == OS.Windows && OS.CURRENT.isAtLeast(10, 0);
    public static final boolean isMac = OS.CURRENT == OS.macOS;
    public static final boolean isX86Compatible = isX86 || isX64;
    public static final boolean isM1 = SystemInfo.isMac && CpuArch.isArm64();
    // Note: Idea >=231 uses Java 17, which requires Mojave to be installed
    public static final boolean isMacOSMojave = isMac;
    public static final boolean isMacOSCatalina = OS.CURRENT == OS.macOS && OS.CURRENT.isAtLeast(10, 15);
    public static final boolean isLinux = OS.CURRENT == OS.Linux;
    public static final boolean isGNOME = SystemInfo.isGNOME;
    public static final boolean isGtk = SystemInfo.isGNOME || SystemInfo.isXfce || SystemInfo.isI3;
}

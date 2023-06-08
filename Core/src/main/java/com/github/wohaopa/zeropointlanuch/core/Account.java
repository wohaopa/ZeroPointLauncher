/*
 * MIT License
 * Copyright (c) 2023 初夏同学
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.wohaopa.zeropointlanuch.core;

import java.util.ArrayList;
import java.util.List;

import com.github.wohaopa.zeropointlanuch.core.auth.Auth;
import com.github.wohaopa.zeropointlanuch.core.auth.OfflineAuth;

public class Account {

    private static final List<Auth> auths = new ArrayList<>();

    private static Auth cur;

    static {
        List<String> list = Config.getConfig()
            .getAccounts();
        if (list == null) {
            Config.getConfig()
                .setAccounts(new ArrayList<>());

        } else if (!list.isEmpty()) {
            list.forEach(s -> auths.add(new OfflineAuth(s)));
        }
    }

    public static List<Auth> getAuths() {
        return auths;
    }

    public static void add(Auth auth) {
        auths.add(auth);
        Config.getConfig()
            .getAccounts()
            .add(auth.getName());
    }

    public static void select(Auth value) {
        cur = value;
    }

    public static Auth getCur() {
        return cur;
    }
}

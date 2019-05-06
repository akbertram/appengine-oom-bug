/*
 * Copyright 2017 Google Inc.
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

package com.example.appengine.java8;

// [START example]

import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@WebServlet(name = "CacheServlet", value = "/fetch")
public class FetchServlet extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(FetchServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    LOGGER.info("Free memory = " + Runtime.getRuntime().freeMemory());

    if(ThreadLocalRandom.current().nextDouble() < 0.5) {
      LOGGER.info("Easy request, should succeed");
    }

    int count = Integer.parseInt(request.getParameter("count"));

    AsyncMemcacheService memcacheService = MemcacheServiceFactory.getAsyncMemcacheService();

    List<Future<Object>> results = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      Future<Object> result = memcacheService.get(Integer.toString(count));
      results.add(result);
    }

    double total = 0;

    for (Future<Object> future : results) {
      List<MyBean> result;
      try {
        result = (List<MyBean>) future.get();
      } catch (Exception e) {
        throw new IOException(e);
      }
    }

    List<MyBean> list = new ArrayList<>();
    for (int i = 0; i < 100_000; i++) {
      list.add(new MyBean());
    }

    LOGGER.info("Free memory = " + Megabytes.toString(Runtime.getRuntime().freeMemory()));
    LOGGER.info("Total = " + total);


  }

}
// [END example]

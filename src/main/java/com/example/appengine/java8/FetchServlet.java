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

/**
 * Fetch large chunks of data from Memcache
 */
@WebServlet(name = "FetchServlet", value = "/fetch")
public class FetchServlet extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(FetchServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    AsyncMemcacheService memcacheService = MemcacheServiceFactory.getAsyncMemcacheService();

    LOGGER.info("Free memory = " + Megabytes.toString(Runtime.getRuntime().freeMemory()));

    int count = Integer.parseInt(request.getParameter("count"));

    if(ThreadLocalRandom.current().nextDouble() < .25) {
      LOGGER.info("Easy request, no memcache, should succeed");
      return;

    } else if(ThreadLocalRandom.current().nextDouble() < 0.50) {
      LOGGER.info("Simple memcache call");
      try {
        memcacheService.put("dummy", "new dummy value").get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

    } else {

      // Otherwise big memory-hogging call
      LOGGER.severe("About to run out of memory....");

      List<Future<Object>> results = new ArrayList<>();

      for (int i = 0; i < 500; i++) {
        Future<Object> result = memcacheService.get(Integer.toString(i));
        results.add(result);
      }

      // Ensure the results are used
      int successCount = 0;
      for (Future<Object> result : results) {
        try {
          result.get();
          successCount++;
        } catch (Throwable caught) {
          throw new RuntimeException(caught);
        }
      }
      LOGGER.info("successCount = " + successCount);
    }


    LOGGER.info("Free memory = " + Megabytes.toString(Runtime.getRuntime().freeMemory()));

  }

}
// [END example]

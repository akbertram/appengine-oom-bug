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


import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.modules.ModulesServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fetch large chunks of data from Memcache
 */
@WebServlet(name = "FetchServlet", value = "/fetch")
public class FetchServlet extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(FetchServlet.class.getName());

  private volatile byte[] SINK;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setHeader("X-Instance",
        ModulesServiceFactory.getModulesService().getCurrentInstanceId());

    int count = Integer.parseInt(request.getParameter("count"));
    if(count == 0) {
      return;
    }

    try {
      doGet(count, response);
    } catch (Error e) {
      throw new RuntimeException(e);
    }
  }

  private void doGet(int count, HttpServletResponse response) {

    LOGGER.info("Free memory = " + Runtime.getRuntime().freeMemory());

    AsyncMemcacheService memcacheService = MemcacheServiceFactory.getAsyncMemcacheService();

    List<Future<Object>> results = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      Future<Object> result = memcacheService.get(Integer.toString(i));
      results.add(result);
    }

    // Ensure the results are used
    int successCount = 0;
    int errorCount = 0;
    for (Future<Object> result : results) {
      try {
        result.get();
        successCount++;
      } catch (Exception e) {
        if(errorCount == 0) {
          LOGGER.log(Level.SEVERE, "Exception fetching from memcache", e);
        }
        errorCount++;
      }
    }

    LOGGER.info("Free memory = " + Megabytes.toString(Runtime.getRuntime().freeMemory()));
    LOGGER.info("successCount = " + successCount);
    if(errorCount > 0) {
      LOGGER.severe("errorCount = " + errorCount);
      response.setStatus(500);
    } else {
      response.setStatus(200);
    }
  }

}

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
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Fill memcache with data
 */
@WebServlet(name = "CacheServlet", value = "/cache")
public class CacheServlet extends HttpServlet {

  private static final Logger LOGGER = Logger.getLogger(CacheServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


    String memcacheKey = request.getParameter("key");

    List<MyBean> list = new ArrayList<>();
    for (int i = 0; i < 5_000; i++) {
      list.add(new MyBean());
    }

    MemcacheServiceFactory.getMemcacheService().put(memcacheKey, list);

    LOGGER.info("Cached. " +  Megabytes.toString(Runtime.getRuntime().freeMemory()));

    // If this is the last one, start fetching.
    if(memcacheKey.equals("999")) {
      startFetching();
    }
  }

  private void startFetching() {

    // Fetch it all to provoke an OOM
    Queue queue = QueueFactory.getDefaultQueue();

    for (int i = 0; i < 3000; i++) {
      for(int j=0;j < 3; j++) {
        queue.add(TaskOptions.Builder.withDefaults()
            .url("/fetch")
            .method(TaskOptions.Method.GET)
            .param("count", Integer.toString(i)));
      }
    }
  }

}
// [END example]

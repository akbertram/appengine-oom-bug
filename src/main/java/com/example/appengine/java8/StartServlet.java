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


import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@WebServlet(name = "Start", value = "/start")
public class StartServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Queue queue = QueueFactory.getDefaultQueue();

    if(request.getParameter("task").equals("cache")) {

      // Fill memcache with lots of data

      for (int i = 0; i < 1000; i++) {
        queue.add(TaskOptions.Builder.withDefaults()
            .url("/cache")
            .method(TaskOptions.Method.GET)
            .param("key", Integer.toString(i)));
      }

    } else {

      // Fetch it all to provoke an OOM

      for (int i = 0; i < 1000; i++) {
        for(int j=0;j < 3; j++) {
        queue.add(TaskOptions.Builder.withDefaults()
            .url("/fetch")
            .method(TaskOptions.Method.GET)
            .param("count", Integer.toString(i))
            .countdownMillis(TimeUnit.MINUTES.toMillis(5)));
        }
      }
    }
  }
}

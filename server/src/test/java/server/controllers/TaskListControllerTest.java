/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.repositories.TaskListRepository;

public class TaskListControllerTest {

    public int nextInt;
    private TestTaskListRepository repo;
    private TaskListController sut;

    private TaskList test;

    @BeforeEach
    public void setup() {
        repo = new TestTaskListRepository();
        sut = new TaskListController(repo);

        test = new TaskList("test");
        sut.add(test);
    }

    @Test
    public void addPersonSuccess() {
        var actual = sut.add(new TaskList("null"));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void addPersonSuccess2() {
        var actual = sut.add(new TaskList(null));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void getIdTest() {
        var actual = sut.add(new TaskList(null));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void getAllTest() {
        var actual = sut.getAll();
    }

    @Test
    public void updateTest() {
        TaskList updated = new TaskList(0L,"john");
        var actual = sut.update(0L, updated);

        List<TaskList> list = new ArrayList<>();
        list.add(updated);
        assertEquals(sut.getAll(), list);
    }

    @Test
    public void deleteTest() {
        var actual = sut.add(new TaskList(null));
        assertEquals(OK, actual.getStatusCode());
    }

}
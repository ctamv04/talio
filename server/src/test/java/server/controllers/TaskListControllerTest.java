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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.util.ArrayList;
import java.util.List;

import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TaskListControllerTest {

    private TestTaskListRepository repo;
    private TaskListController sut;

    private List<TaskList> tasks = new ArrayList<>();

    @BeforeEach
    public void setup() {
        repo = new TestTaskListRepository();
        sut = new TaskListController(repo);

        for(int i = 0; i < 2; i++){
            tasks.add(new TaskList());
            repo.save(tasks.get(i));
        }

    }

    @Test
    public void addTaskListSuccess() {
        var actual = sut.add(new TaskList("null"));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void addTaskListSuccess2() {
        var actual = sut.add(new TaskList(null));
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    public void getIdTest() {

        assertSame(tasks.get(0), sut.getById(tasks.get(0).getId()).getBody());

    }

    @Test
    public void getIdTestFail() {
        assertEquals(BAD_REQUEST, sut.getById(2).getStatusCode());
    }

    @Test
    public void getAllTest() {
        List<TaskList> test_list = sut.getAll();

        for(int i = 0; i < 1; i++){
            assertEquals(test_list.get(i), tasks.get(i));
        }
    }

    @Test
    public void updateTest() {
        TaskList updated = tasks.get(0);
        updated.setName("updated");
        var actual = sut.update(tasks.get(0).getId(), updated);
        assertEquals(updated.getName(), sut.getById(updated.getId()).getBody().getName());
    }

    @Test
    public void deleteTest() {
        var actual = sut.delete(tasks.get(0).getId());
        assertFalse(sut.getAll().contains(tasks.get(0)));
    }

}
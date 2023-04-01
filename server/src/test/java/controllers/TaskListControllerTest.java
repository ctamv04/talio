import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import models.Board;
import models.TaskCard;
import models.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import server.controllers.BoardController;
import server.controllers.TaskListController;
import server.repositories.TaskListRepository;
import server.services.LongPollingService;
import server.services.TaskListService;

//public class TaskListControllerTest {
//
//    @Mock
//    private TaskListRepository listRepo;
//    @Mock
//    private LongPollingService longPollingMock;
//    @Mock
//    private TaskListService service;
//    @InjectMocks
//    private BoardController sut;

//    /**
//     *Setup method for the test methods
//     */
//    @BeforeEach
//    public void setup() {
//
//        listRepo = Mockito.mock(TaskListRepository.class);
//        service = Mockito.mock(TaskListService.class);
//        longPollingMock = Mockito.mock(LongPollingService.class);
//
//        Board board = new Board();
//        board.setId(1L);
//
//        TaskList list1 =
//
//        TestTaskListRepository repo = new TestTaskListRepository();
//        TaskListService service = new TaskListService(repo, board_repo);
//        sut = new TaskListController(repo, service);
//
//        for(int i = 0; i < 2; i++){
//            tasks.add(new TaskList());
//            repo.save(tasks.get(i));
//        }
//
//    }
//
//    /**
//     *Success case for TaskList addition
//     */
//    @Test
//    public void addTaskListSuccess() {
//        var actual = sut.add(new TaskList("null", board), board.getId());
//        assertEquals(OK, actual.getStatusCode());
//    }
//
//    /**
//     *Success case for TaskList addition
//     */
//    @Test
//    public void addTaskListSuccess2() {
//        var actual = sut.add(new TaskList(null, board), board.getId());
//        assertEquals(OK, actual.getStatusCode());
//    }
//
//    /**
//     *Success case for TaskCard retrieval
//     */
//    @Test
//    public void getTaskCardsTest() {
//        TaskList list = tasks.get(0);
//        list.setTaskCards(List.of(new TaskCard("card 1", list)));
//
//        assertEquals(tasks.get(0).getTaskCards(), sut.getTaskCard((long) 0).getBody());
//    }
//
//    /**
//     *Success case for TaskList retrieval
//     */
//    @Test
//    public void getIdTest() {
//
//        assertSame(tasks.get(0), sut.getById(tasks.get(0).getId()).getBody());
//
//    }
//
//    /**
//     *Fail case for TaskList retrieval
//     */
//    @Test
//    public void getIdTestFail() {
//        assertEquals(BAD_REQUEST, sut.getById(2L).getStatusCode());
//    }
//
//    /**
//     *Success case for mass TaskList retrieval
//     */
//    @Test
//    public void getAllTest() {
//        List<TaskList> test_list = sut.getAll();
//
//        for(int i = 0; i < 1; i++){
//            assertEquals(test_list.get(i), tasks.get(i));
//        }
//    }
//
//    /**
//     *Success case for TaskList update
//     */
//    @Test
//    public void updateTest() {
//        TaskList updated = tasks.get(0);
//        updated.setName("updated");
//        ResponseEntity<TaskList> response=sut.getById(updated.getId());
//        assertNotNull(response.getBody());
//        assertEquals(updated.getName(), response.getBody().getName());
//    }
//
//    /**
//     *Fail case for TaskList deletion
//     */
//    @Test
//    public void deleteTestFalse() {
//        ResponseEntity<TaskList> response=sut.delete(5L);
//        assertEquals(BAD_REQUEST,response.getStatusCode());
//    }
//
////    /**
////     *Success case for TaskList deletion
////     */
////    @Test
////    public void deleteTest() {
////        sut.delete(0L);
////        assertFalse(sut.getAll().contains(tasks.get(0)));
////    }
//
//
//    /**
//     *Fail case for TaskList update
//     */
//    @Test
//    public void updateTestFalse() {
//        sut.update(0L,tasks.get(1));
//        assertEquals(tasks.get(1),sut.getAll().get(0));
//    }
//
//}
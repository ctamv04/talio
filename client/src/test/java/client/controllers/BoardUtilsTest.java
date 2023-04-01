package client.controllers;

import client.utils.BoardUtils;
import client.utils.ServerUtils;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BoardUtilsTest {
    @Mock
    private MainCtrl mainCtrlMock;
    @Mock
    private ServerUtils serverUtilsMock;
    @InjectMocks
    private BoardUtils sut;

    @BeforeEach
    void setUp() {
        mainCtrlMock=mock(MainCtrl.class);
        serverUtilsMock=mock(ServerUtils.class);
        sut=new BoardUtils(mainCtrlMock,serverUtilsMock);
    }

    @Test
    public void testConvertScenesFromTaskListIds(){
        List<Long> ids= List.of(1L,2L,3L);
        Map<Long, Parent> map=new HashMap<>();
        BoardController boardControllerMock=mock(BoardController.class);
        List<TaskListController> taskListControllers=new ArrayList<>();

        when(mainCtrlMock.createTaskList(1L,boardControllerMock)).thenReturn(new Pair<>(mock(TaskListController.class), new AnchorPane()));
        when(mainCtrlMock.createTaskList(2L,boardControllerMock)).thenReturn(new Pair<>(mock(TaskListController.class), new AnchorPane()));
        when(mainCtrlMock.createTaskList(3L,boardControllerMock)).thenReturn(new Pair<>(mock(TaskListController.class), new AnchorPane()));
        List<Parent> sol=sut.convertScenesFromTaskListIds(ids,map,boardControllerMock,taskListControllers);
        assertEquals(3,sol.size());
    }

    @Test
    public void testClosePolling(){
        try{
            ExecutorService serviceMock=mock(ExecutorService.class);
            sut.closePolling(serviceMock);
            verify(serviceMock,times(1)).shutdown();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

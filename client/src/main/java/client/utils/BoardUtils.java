package client.utils;

import client.controllers.BoardController;
import client.controllers.MainCtrl;
import client.controllers.TaskListController;
import com.google.inject.Inject;
import jakarta.ws.rs.core.GenericType;
import javafx.application.Platform;
import javafx.scene.Parent;
import models.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BoardUtils {
    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;

    @Inject
    public BoardUtils(MainCtrl mainCtrl, ServerUtils serverUtils){
        this.mainCtrl=mainCtrl;
        this.serverUtils = serverUtils;
    }

    public List<Parent> convertScenesFromTaskListIds(List<Long> ids, Map<Long, Parent> taskListCache,
                                                     BoardController boardController, List<TaskListController> taskListControllers) {
        List<Parent> list = new ArrayList<>();
        for (var id : ids) {
            if (!taskListCache.containsKey(id)) {
                var taskListPair = mainCtrl.createTaskList(id, boardController);
                taskListControllers.add(taskListPair.getKey());
                taskListCache.put(id, taskListPair.getValue());
                boardController.getTaskListControllerMap().put(id,taskListPair.getKey());
            }
            list.add(taskListCache.get(id));
        }
        return list;
    }

    public void registerDetailsUpdates(Consumer<Board> consumer, Board board,
                                       ExecutorService detailUpdatesExecutor, BoardController boardController){
        detailUpdatesExecutor.submit(()->{
            while(!detailUpdatesExecutor.isShutdown()){
                var response=serverUtils.getBoardUpdates(board.getId());
                if(response.getStatus()==204)
                    continue;
                if(response.getStatus()==400){
                    boardController.closePolling();
                    Platform.runLater(() -> {
                        if(boardController.isActive()) {
                            mainCtrl.showLoginPage();
                            mainCtrl.showDeletedBoard();
                        }
                        else
                            boardController.setActive(false);
                    });
                    return;
                }
                consumer.accept(response.readEntity(Board.class));
            }
        });
    }

    public void registerTaskListIdsUpdates(Consumer<List<Long>> consumer, Board board,
                                           ExecutorService taskListIdsUpdatesExecutor){
        taskListIdsUpdatesExecutor.submit(()->{
            while (!taskListIdsUpdatesExecutor.isShutdown()){
                var response=serverUtils.getTaskListIdsUpdates(board.getId());
                if(response.getStatus()==204)
                    continue;
                List<Long> ids=response.readEntity(new GenericType<>() {});
                consumer.accept(ids);
            }
        });
    }

    public void closePolling(ExecutorService service){
        if(service!=null)
            service.shutdown();
    }

}

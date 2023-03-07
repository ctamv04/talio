package server.configs;

import models.Board;
import models.TaskCard;
import models.TaskList;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.repositories.BoardRepository;
import server.repositories.TaskCardRepository;
import server.repositories.TaskListRepository;

import java.util.List;

@Configuration
public class H2MemConfig {
    @Bean
    CommandLineRunner commandLineRunner(BoardRepository boardRepository,
                                        TaskListRepository taskListRepository,
                                        TaskCardRepository taskCardRepository){
        return args -> {
            Board board1=new Board("Board1");
            Board board2=new Board("Board2");
            Board board3=new Board("Board3");
            TaskList taskList1=new TaskList("In Progress");
            TaskList taskList2=new TaskList("Done");
            TaskList taskList3=new TaskList("Unfinished");
            TaskCard taskCard1=new TaskCard("Prepare");


            boardRepository.saveAll(List.of(board1,board2,board3));
            taskListRepository.saveAll(List.of(taskList1,taskList2,taskList3));

            board1.setTaskLists(List.of(taskList1,taskList2));
            board2.setTaskLists(List.of(taskList3));
            boardRepository.saveAll(List.of(board1,board2,board3));

            taskCardRepository.saveAll(List.of(taskCard1));
            taskList1.setTaskCards(List.of(taskCard1));
            taskListRepository.saveAll(List.of(taskList1,taskList2,taskList3));
        };
    }
}

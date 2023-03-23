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
            TaskList taskList1=new TaskList("In Progress",board1);
            TaskList taskList2=new TaskList("Done",board1);
            TaskList taskList3=new TaskList("Unfinished",board2);
            TaskCard taskCard1=new TaskCard("Prepare",taskList1,0);
            TaskCard taskCard2=new TaskCard("Study",taskList1,1);
            TaskCard taskCard3=new TaskCard("Read",taskList1,2);
            TaskCard taskCard4=new TaskCard("Exercise",taskList2,0);
            TaskCard taskCard5=new TaskCard("Dunno",taskList2,1);

            boardRepository.saveAll(List.of(board1,board2,board3));
            taskListRepository.saveAll(List.of(taskList1,taskList2,taskList3));

            board1.getTaskLists().addAll(List.of(taskList1,taskList2));
            board2.getTaskLists().add(taskList3);
            boardRepository.saveAll(List.of(board1,board2,board3));

            taskCardRepository.saveAll(List.of(taskCard1,taskCard2,taskCard3,taskCard4,taskCard5));
            taskList1.getTaskCards().addAll(List.of(taskCard1,taskCard2,taskCard3));
            taskList2.getTaskCards().addAll(List.of(taskCard4,taskCard5));
            taskListRepository.saveAll(List.of(taskList1,taskList2,taskList3));
        };
    }
}

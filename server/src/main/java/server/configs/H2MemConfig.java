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
import java.util.Random;

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
            boardRepository.saveAll(List.of(board1,board2,board3));
            for(int i=0;i<6;i++){
                Random random=new Random();
                TaskList taskList=new TaskList(String.valueOf(random.nextInt(1000,9999)),board1);
                board1.getTaskLists().add(taskList);
                taskListRepository.save(taskList);
                for(int j=0;j<5;j++){
                    TaskCard taskCard=new TaskCard(String.valueOf(random.nextInt(1000,9999)),taskList,j);
                    taskList.getTaskCards().add(taskCard);
                    taskCardRepository.save(taskCard);
                }
            }
        };
    }
}

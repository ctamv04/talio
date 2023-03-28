package server.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class LongPollingService {

    public DeferredResult<ResponseEntity<List<Long>>> getIdsUpdates(Long parentId,
                                                                    Map<Long, Map<Object, Consumer<List<Long>>>> idsListeners){
        var noContent=ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res=new DeferredResult<ResponseEntity<List<Long>>>(10000L,noContent);

        var key=new Object();
        Map<Object, Consumer<List<Long>>> listeners=idsListeners.get(parentId);
        if(listeners==null){
            listeners=new ConcurrentHashMap<>();
            idsListeners.put(parentId,listeners);
        }
        listeners.put(key,list -> res.setResult(ResponseEntity.ok(list)));
        res.onCompletion(()-> idsListeners.get(parentId).remove(key));

        return res;
    }
}

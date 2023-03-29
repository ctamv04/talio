package server.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class LongPollingService {

    public <T> DeferredResult<ResponseEntity<T>> getUpdates(Long id, Map<Long, Map<Object, Consumer<T>>> listeners){
        var noContent=ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res=new DeferredResult<ResponseEntity<T>>(10000L,noContent);

        var key=new Object();
        Map<Object, Consumer<T>> listenersForSpecificId=listeners.get(id);
        if(listenersForSpecificId==null){
            listenersForSpecificId=new ConcurrentHashMap<>();
            listeners.put(id,listenersForSpecificId);
        }
        listenersForSpecificId.put(key,entity -> {
            if(entity==null)
                res.setResult(ResponseEntity.badRequest().build());
            res.setResult(ResponseEntity.ok(entity));
        });
        res.onCompletion(()-> listeners.get(id).remove(key));

        return res;
    }

    public <T> void registerUpdate(Map<Object,Consumer<T>> listeners, T entity){
        if(listeners==null)
            return;
        listeners.forEach((key,consumer)->{
            consumer.accept(entity);
            listeners.remove(key);
        });
    }
}
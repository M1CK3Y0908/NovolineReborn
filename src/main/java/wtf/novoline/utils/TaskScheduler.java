package wtf.novoline.utils;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import wtf.novoline.Novoline;
import wtf.novoline.event.RunTickEvent;

public final class TaskScheduler {
   private final List<ScheduledTask> tasks = new CopyOnWriteArrayList<>();
   @EventLink
   public final Listener<RunTickEvent> onRunTick = runTickEvent -> this.tasks.forEach(scheduledTask -> {
      if (scheduledTask.isDue()) {
         scheduledTask.run();
         this.tasks.remove(scheduledTask);
      } else {
         scheduledTask.onWaiting();
      }
   });

   public void schedule(ScheduledTask scheduledTask2) {
      this.tasks.add(scheduledTask2);
   }

   public TaskScheduler() {
      Novoline.getNovoline().getBus().subscribe(this);
   }
}

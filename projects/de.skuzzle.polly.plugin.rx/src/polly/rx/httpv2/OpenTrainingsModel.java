package polly.rx.httpv2;

import java.util.List;

import de.skuzzle.polly.http.api.HttpEvent;
import polly.rx.core.TrainManagerV2;
import polly.rx.entities.TrainEntityV3;


public class OpenTrainingsModel extends TrainingTableModel {

    public OpenTrainingsModel(TrainManagerV2 trainManager) {
        super(trainManager);
    }

    
    
    @Override
    public List<TrainEntityV3> getData(HttpEvent e) {
        final String forUser = e.get(FOR_USER);
        return this.trainManager.getAllOpenTrains(forUser).getTrains();
    }
}

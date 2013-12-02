package polly.rx.httpv2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import polly.rx.MSG;
import polly.rx.core.TrainManagerV2;
import polly.rx.entities.TrainEntityV3;
import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.html.AbstractHTMLTableModel;
import de.skuzzle.polly.sdk.time.Milliseconds;
import de.skuzzle.polly.sdk.util.DirectedComparator.SortOrder;


public class TrainingTableModel extends AbstractHTMLTableModel<TrainEntityV3> {

    private final static String HEADER[] = MSG.trainingModelColumns.split(","); //$NON-NLS-1$
    
    protected final static String FOR_USER = "forUser"; //$NON-NLS-1$
    
    protected final TrainManagerV2 trainManager;
    
    
    public TrainingTableModel(TrainManagerV2 trainManager) {
        this.trainManager = trainManager;
    }
    
    
    
    @Override
    public boolean isFilterable(int column) {
        return true;
    }
    
    
    
    @Override
    public boolean isSortable(int column) {
        return true;
    }
    
    
    
    @Override
    public int getDefaultSortColumn() {
        return HEADER.length - 1;
    }
    
    
    
    @Override
    public SortOrder getDefaultSortOrder() {
        return SortOrder.DESCENDING;
    }
    
    
    
    @Override
    public String getHeader(int column) {
        return HEADER[column];
    }
    
    

    @Override
    public int getColumnCount() {
        return HEADER.length;
    }
    
    

    @Override
    public Object getCellValue(int column, TrainEntityV3 element) {
        switch (column) {
        case 0:
            final User trainer = this.trainManager.getTrainer(element.getTrainerId());
            return trainer.getName();
        case 1: return element.getType();
        case 2: return element.getCurrentValue();
        case 3: return element.getFactor();
        case 4: return element.getCosts();
        case 5: return element.getFactor() * element.getCosts();
        case 6: 
            final long end = element.getTrainFinished().getTime();
            final long start = element.getTrainStart().getTime();
            return new Types.TimespanType(Milliseconds.toSeconds(end - start));
        case 7: return element.getTrainStart();
        }
        return null;
    }
    
    
    
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 3: return Double.class;
        case 5: return Double.class;
        case 6: return Types.class;
        case 7: return Date.class;
        default:
            break;
        }
        return super.getColumnClass(column);
    }
    
    
    
    @Override
    public Map<String, String> getRequestParameters(HttpEvent e) {
        final Map<String, String> m = new HashMap<>();
        m.put(FOR_USER, e.get(FOR_USER));
        return m;
    }
    
    

    @Override
    public List<TrainEntityV3> getData(HttpEvent e) {
        final String forUser = e.get(FOR_USER);
        return this.trainManager.getClosedTrains(forUser).getTrains();
    }
}

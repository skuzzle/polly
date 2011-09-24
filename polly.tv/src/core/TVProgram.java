package core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;


public class TVProgram extends XMLEntity {
    
    private String channel;

    private String name;
    
    private Date time;
        
    private String duration;
    
    private String description;
    
    private String episode;
    
    private String genre;

    public TVProgram(Element e, Date date, String onChannel) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar today = Calendar.getInstance();
        today.setTime(date);
        

        try {
            Calendar c2 = Calendar.getInstance();
            c2.setTime(df.parse(this.getTextValue(e, "time")));
            c2.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
            c2.set(Calendar.SECOND, 0);
            this.time = c2.getTime();

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        this.channel = onChannel;
        this.name = this.getTextValue(e, "name");
        this.duration = this.getTextValue(e, "duration");
        this.description = this.getTextValue(e, "description");
        this.episode = this.getTextValue(e, "episode");
        this.genre = this.getTextValue(e, "category");
    }
    
    
    
    public String getChannel() {
        return channel;
    }
    
    
    
    public void setChannel(String channel) {
        this.channel = channel;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public Date getTime() {
        return time;
    }

    
    public void setTime(Date time) {
        this.time = time;
    }

    
    public String getDuration() {
        return duration;
    }

    
    public void setDuration(String duration) {
        this.duration = duration;
    }

    
    public String getDescription() {
        return description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

    
    public String getEpisode() {
        return episode;
    }

    
    public void setEpisode(String episode) {
        this.episode = episode;
    }

    
    public String getGenre() {
        return genre;
    }

    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Name: ");
        b.append(this.name);
        b.append("\nZeit: ");
        b.append(this.time);
        b.append("\nDuration: ");
        b.append(this.duration);
        //b.append("\nDescription: ");
        //b.append(this.description);
        b.append("\nEpisode: ");
        b.append(this.episode);
        b.append("\nGenre: ");
        b.append(this.genre);
        return b.toString();
    }
}
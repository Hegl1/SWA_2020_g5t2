package at.qe.skeleton.ui.beans;

import at.qe.skeleton.model.Media;
import at.qe.skeleton.model.MediaType;
import at.qe.skeleton.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collection;

@Component
@Scope("view")
public class SearchMediaBean implements Serializable {
    private Collection<Media> results;

    private String search = null;
    private String filterType = null;
    private String filterLanguage = null;
    private String filterAvailable = null;

    public Collection<Media> getResults() {
        return results;
    }

    @Autowired
    private MediaService mediaService;

    @PostConstruct
    public void init() {
        this.results = mediaService.getAllMedia();
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(final String search) {
        this.search = search == null ? null : search.trim();

        if(search != null && this.search.isEmpty()){
            this.search = null;
        }
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(final String filterType) {
        this.filterType = filterType == null ? null : filterType.trim().toUpperCase();

        if(filterType != null && this.filterType.isEmpty()){
            this.filterType = null;
        }
    }

    public String getFilterLanguage() {
        return filterLanguage;
    }

    public void setFilterLanguage(final String filterLanguage) {
        this.filterLanguage = filterLanguage == null ? null : filterLanguage.trim();

        if(filterLanguage != null && this.filterLanguage.isEmpty()){
            this.filterLanguage = null;
        }
    }

    public String getFilterAvailable() {
        return filterAvailable;
    }

    public void setFilterAvailable(final String filterAvailable) {
        this.filterAvailable = filterAvailable == null ? null : filterAvailable.trim();

        if(filterAvailable != null && this.filterAvailable.isEmpty()){
            this.filterAvailable = null;
        }
    }

    private MediaType getFilterMediaType(){
        if(filterType == null) return null;

        switch (filterType){
            case "BOOK": return MediaType.BOOK;
            case "AUDIOBOOK": return MediaType.AUDIOBOOK;
            case "VIDEO": return MediaType.VIDEO;
            case "MAGAZINE": return MediaType.MAGAZINE;
            default: return null;
        }
    }

    public void doFilter(){
        Collection<Media> results = mediaService.getAllMedia();

        if(search != null){
            results = mediaService.filterMediaByTitle(results, search);
        }

        MediaType filterType = getFilterMediaType();

        if(filterType != null){
            results = mediaService.filterMediaByType(results, filterType);
        }

        if(filterLanguage != null){
            results = mediaService.filterMediaByLanguage(results, filterLanguage);
        }

        if(filterAvailable != null){
            results = mediaService.filterMediaByAvailability(results, filterAvailable.equalsIgnoreCase("YES"));
        }

        this.results = results;
    }

    public void doResetFilter(){
        this.search = null;
        this.filterType = null;
        this.filterLanguage = null;
        this.filterAvailable = null;

        this.init();
    }
}
package rw.bk.taxi24.api.service.dto;

public class TripUpdateDTO {

    private Long id;
    private String newStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}

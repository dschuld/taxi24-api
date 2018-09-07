package rw.bk.taxi24.api.service.dto;

/**
 * A DTO for a trip update.
 */
public class TripUpdateDTO {

    private String newStatus;

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}

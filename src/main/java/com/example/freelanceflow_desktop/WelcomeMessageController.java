package com.example.freelanceflow_desktop;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;

public class WelcomeMessageController {

    @FXML
    private AnchorPane rootPane;
    
    @FXML
    private ProgressBar progressBar;

    @FXML
    private ImageView welcomeImage;

    private String userRole;

    // setUserRole may be called before FXML injections are complete; defer UI work
    public void setUserRole(String role) {
        this.userRole = role;
        // ensure injected controls and scene are available on the JavaFX thread
        Platform.runLater(this::startTransition);
    }

    // initialize the progress bar if possible
    @FXML
    public void initialize() {
        if (progressBar != null) {
            progressBar.setProgress(0);
        }
        // reference welcomeImage to avoid 'assigned but never accessed' warning
        if (welcomeImage != null) {
            welcomeImage.setPreserveRatio(true);
        }
    }

    private void startTransition() {
        if (progressBar == null) {
            System.err.println("progressBar is null - check fx:id in FXML and controller wiring.");
            return;
        }

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(2), new KeyValue(progressBar.progressProperty(), 1))
        );
        
        timeline.setOnFinished(event -> {
            navigateToDetailsPage();
            // consume the event to mark it handled (and avoid unused-parameter warnings)
            event.consume();
        });
        timeline.play();
    }

    private void navigateToDetailsPage() {
        try {
            System.out.println("Navigating to details page. User role: " + userRole);

            String fxmlFile = "client".equals(userRole) 
                ? "ClientDetails.fxml" 
                : "FreelancerDetails.fxml";
            
            String resourcePath = "/com/example/freelanceflow_desktop/" + fxmlFile;
            System.out.println("Loading FXML: " + resourcePath);

            URL fxmlUrl = getClass().getResource(resourcePath);
            if (fxmlUrl == null) {
                System.err.println("FXML resource not found: " + resourcePath + " - check path and that file is on the classpath");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            // try to obtain the stage from rootPane first, then progressBar
            Stage stage = null;
            if (rootPane != null && rootPane.getScene() != null) {
                stage = (Stage) rootPane.getScene().getWindow();
            } else if (progressBar != null && progressBar.getScene() != null) {
                stage = (Stage) progressBar.getScene().getWindow();
            }

            if (stage == null) {
                System.err.println("Cannot obtain Stage - scene/window is null. Navigation aborted.");
                return;
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
            System.out.println("Navigation successful!");

        } catch (IOException e) {
            System.err.println("Error navigating to details page:");
            e.printStackTrace();
        }
    }
}

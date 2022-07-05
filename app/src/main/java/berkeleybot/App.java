

package berkeleybot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.security.auth.login.LoginException;


import org.neuroph.core.NeuralNetwork;
import org.neuroph.imgrec.ImageRecognitionPlugin;
import org.neuroph.imgrec.ImageSizeMismatchException;
import org.neuroph.nnet.MultiLayerPerceptron;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class App extends ListenerAdapter{

    MultiLayerPerceptron berknet = (MultiLayerPerceptron) NeuralNetwork.createFromFile("app/src/main/java/berkeleybot/berknet.nnet");
    ImageRecognitionPlugin imageRecognition = (ImageRecognitionPlugin)berknet.getPlugin(ImageRecognitionPlugin.class);

    public static void main(String[] args) {
        //setup neural network
        

        JDABuilder builder = JDABuilder.createDefault("OTkyNDQ2MDI0NDYxMzIwMzMz.GVtLeI.vaaGvRwpuL6xCYFfwYvBis-G1HNQb1fpxRt6Tc");

        builder.setActivity(Activity.watching("Berkeley"));
        builder.addEventListeners(new App());
        try {
            builder.build();
        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override //Notaduck#9706
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().getName().equals("Notaduck") && event.getAuthor().getDiscriminator().equals("9706")) {
            event.getMessage().addReaction(Emoji.fromUnicode("U+1F602")).queue();
            MessageChannel channel = event.getChannel();     
        } else {
            System.out.println("not berk:(");
        }

        if (event.getMessage().getAttachments().isEmpty()) {
            System.out.println("no attachments :(");
        } else {
            File berkImage = new File("app/src/main/java/berkeleybot/berkImage");
            System.out.println("attachments!");
            //time to neural network
            event.getMessage().getAttachments().get(0).getProxy().downloadToFile(berkImage);
            String outputValues = recognizeImage(berkImage);
            String[] splitValues = outputValues.split(",");
            for (int i = 0; i < splitValues.length; i++) {
                if (splitValues[i].charAt(3) == '9' || splitValues[i].charAt(3) == '8' || splitValues[i].charAt(3) == '7') {
                    event.getChannel().sendMessage("berkeley bot is " + splitValues[i].substring(3, 5) + "% sure that this is Berkeley!").queue();
                }
            }

        }
    }

    private String recognizeImage(File image) {
        try {
            HashMap<String, Double> output = imageRecognition.recognizeImage(image);
            return output.values().toString();
        } catch (ImageSizeMismatchException | IOException e) {
            return e.toString();
        }
    }


}

package trial.mycontext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import javax.enterprise.inject.New;
import javax.enterprise.inject.spi.Extension;
import java.net.URL;

@RunWith(Arquillian.class)
@RunAsClient
public class SmokeTest {
    @Drone
    @New
    WebDriver firstWindow;

    @ArquillianResource
    URL contextPath;


    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        BeansDescriptor beansDesc = Descriptors.create(BeansDescriptor.class);
        PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile("pom.xml");
        // to expire session upon undeploy on tomcat too
        StringAsset contextXml = new StringAsset("<Context><Manager pathname=\"\" /></Context>");
        return ShrinkWrap.create(WebArchive.class)
                .addAsWebInfResource(new StringAsset(beansDesc.exportAsString()), beansDesc.getDescriptorName())
                .addAsManifestResource(contextXml, "context.xml")
                .addAsServiceProvider(Extension.class, MyCdiExtension.class)
                .addClasses(MyCdiExtension.class, MyContext.class, MyScope.class, HelloServlet.class, HelloBean.class)
                .addAsLibraries(
                        pom.resolve(
                                "org.apache.deltaspike.core:deltaspike-core-impl")
                                .withTransitivity().asFile())
                ;
    }

    @Test
    public void testHello() throws Exception {
        firstWindow.navigate().to(contextPath + "hello");
        String body = firstWindow.findElement(By.tagName("body")).getText();
        Assert.assertEquals("hello", body);
    }
}

package io.recheck.jobsapp.bookingvet.frontend.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.recheck.jobsapp.bookingvet.backend.dto.UserDetailsDTO;
import io.recheck.jobsapp.bookingvet.backend.service.AdminService;
import io.recheck.jobsapp.bookingvet.frontend.components.ErrorDialog;
import io.recheck.jobsapp.bookingvet.frontend.components.UserDetailsLayout;
import io.recheck.jobsapp.bookingvet.frontend.components.uoiGrid.UserDetailsGrid;
import io.recheck.jobsapp.bookingvet.frontend.components.uoiGrid.UserDetailsGridListeners;

import java.util.List;

@Route(value = "admin", layout = MainView.class)
public class AdminView extends Div {

    private AdminService adminService;

    List<UserDetailsDTO> users;

    private Button newButton = new Button("New User");

    private UserDetailsGrid userDetailsGrid;
    private UserDetailsGridListeners userDetailsGridListeners;
    private VerticalLayout gridLayout;

    private UserDetailsLayout userDetailsLayout = new UserDetailsLayout();

    protected ErrorDialog errorDialog = new ErrorDialog();

    private VerticalLayout viewLayout = new VerticalLayout();

    public AdminView(AdminService adminService) {
        this.adminService = adminService;
        users = adminService.getUsers();
        initListeners();
        initLayout();
    }

    private void initLayout() {
        userDetailsGrid = new UserDetailsGrid(users, userDetailsGridListeners);

        HorizontalLayout horizontalLayout = new HorizontalLayout(new Label("Existing Users"), newButton);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        gridLayout = new VerticalLayout(horizontalLayout, userDetailsGrid);

        viewLayout.add(userDetailsLayout);

        add(new HorizontalLayout(gridLayout, viewLayout));

        applyCss();
    }

    private void applyCss() {
        addClassName("admin-view");

        gridLayout.getStyle().clear();
        gridLayout.addClassName("leftColumnLayout");

        viewLayout.getStyle().clear();
        viewLayout.addClassName("rightColumnLayout");
    }

    private void initListeners() {
        newButton.addClickListener(e -> {
            toInitState();
            userDetailsLayout.toCreateState();
        });

        userDetailsLayout.createClickListener(e -> {
            UserDetailsDTO data = userDetailsLayout.getData();
            try {
                UserDetailsDTO userDetailsResponse = adminService.createUser(data);
                userDetailsGrid.addItem(userDetailsResponse);
                toInitState();
            }
            catch (Exception ex) {
                errorDialog.open(ex.getMessage());
            }
        });

        userDetailsLayout.cancelClickListener(e -> toInitState());

        userDetailsGridListeners = new UserDetailsGridListeners() {
            @Override
            public void disableClickListener(UserDetailsDTO userDetailsDTO) {
                userDetailsDTO.setEnabled(!userDetailsDTO.isEnabled());
                adminService.enableUser(userDetailsDTO);
                userDetailsGrid.refreshUI();
            }

            @Override
            public void deleteClickListener(UserDetailsDTO userDetailsDTO) {
                adminService.deleteUser(userDetailsDTO);
                users.remove(userDetailsDTO);
                userDetailsGrid.refreshUI();
            }
        };
    }

    private void toInitState() {
        userDetailsLayout.setVisible(false);
    }

}

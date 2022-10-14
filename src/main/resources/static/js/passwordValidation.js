function comparePasswords() {
    const passwordValue = $('#password').val();
    const passwordConfirmationValue = $('#passwordConfirmation').val();
    const passwordsDoNotMatchContainer = $('#passwords-do-not-match-container');
    const passwordsDoNotMatchNode = $('#passwords-do-not-match');
    const invalidConfirmationMessageExists = passwordsDoNotMatchNode.length != 0;
    const submitButton = $('#submit');
    if (passwordValue === passwordConfirmationValue && invalidConfirmationMessageExists) {
        passwordsDoNotMatchNode.remove();
        submitButton.prop('disabled', false);
    } else if (passwordValue != passwordConfirmationValue && !invalidConfirmationMessageExists) {
        let p = document.createElement('p');
        p.id = 'passwords-do-not-match';
        p.classList.add('mb-0');
        p.innerHTML = 'Passwords do not match!';
        passwordsDoNotMatchContainer.append(p);
        submitButton.prop('disabled', true);
    }
}
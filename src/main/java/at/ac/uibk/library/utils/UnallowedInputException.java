package at.ac.uibk.library.utils;

public class UnallowedInputException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnallowedInputException(final String message) {
		super(message);
	}
}

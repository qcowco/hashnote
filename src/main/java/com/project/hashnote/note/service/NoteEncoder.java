package com.project.hashnote.note.service;

//@Component
//public class NoteEncoder {
//    private List<AlgorithmDetails> algorithms;
//
//    @Autowired
//    public NoteEncoder(List<AlgorithmDetails> algorithms) {
//        this.algorithms = algorithms;
//    }
////
////    public NoteRequest encodeRequest(NoteRequest noteRequest) {
////        EncryptionDetails encryptionDetails = noteRequest.getEncryptionDetails();
////
////        MessageEncoder messageEncoder = getEncoderFor(encryptionDetails);
////
////        NoteDto plainDto = noteRequest.getNoteDto();
////        byte[] encryptedMessage = messageEncoder.encrypt(plainDto.getContent().getBytes());
////        NoteDto encodedDto = createEncodedDto(plainDto, encryptedMessage);
////
////        EncryptionDetails encodedDetails = createEncodedDetails(messageEncoder);
////        encodedDetails.setMethod(messageEncoder.getMethod());
////
////        return new NoteRequest(encodedDto, encodedDetails);
////    }
//
////    private MessageEncrypterImpl getEncoderFor(EncryptionDetails encryptionDetails) {
////        AlgorithmDetails algorithmDetails = tryGetAlgorithm(encryptionDetails.getMethod());
////
////        MessageEncrypterImpl.EncrypterBuilder builder = MessageEncrypterImpl.builder()
////                .algorithmDetails(algorithmDetails);
////
////        if (encryptionDetails.getKey() != null)
////            builder = builder.secretKey(decodeBase64(decodeBase64(encryptionDetails.getKey())));
////
////        if (encryptionDetails.getVector() != null)
////            builder = builder.initVector(decodeBase64(encryptionDetails.getVector()));
////// TODO: 29.12.2019 oddzielic enkodowanie od buildowania
////        return builder.build();
////    }
//
//    private AlgorithmDetails tryGetAlgorithm(String method) {
//        return algorithms.stream()
//                .filter(alg -> alg.getMethod().equals(method))
//                .findFirst()
//                .orElseThrow(
//                        () -> new InvalidAlgorithmNameException("No algorithm found with name: " + method)
//                );
//    }
//
//    private NoteDto createEncodedDto(NoteDto noteDto, byte[] encryptedMessage) {
//        NoteDto encodedDto = new NoteDto();
//
//        encodedDto.setId(noteDto.getId());
//        encodedDto.setName(noteDto.getName());
//        encodedDto.setContent(encodeBase64String(encryptedMessage));
//
//        return encodedDto;
//    }
//
//    private EncodingDetails createEncodedDetails(MessageEncoder messageEncoder) {
//        EncodingDetails encodingDetails = new EncodingDetails();
//
//        String[] encodedDetails = getEncodedDetailsFor(messageEncoder);
//
//        String encodedKey = encodedDetails[0];
//        String encodedVector = encodedDetails[1];
//
//        encodingDetails.setKey(encodedKey);
//        encodingDetails.setVector(encodedVector);
//
//        return encodingDetails;
//    }
//
//    private String[] getEncodedDetailsFor(MessageEncoder messageEncoder) {
//        String[] encodedDetails = new String[2];
//
//        encodedDetails[0] = encodeBase64String(encodeBase64(messageEncoder.getSecretKey()));
//        encodedDetails[1] = encodeBase64String(messageEncoder.getInitVector());
//
//        return encodedDetails;
//    }
//
////
////    public byte[] decrypt(Note note, String secretKey) {
////        verifyKey(secretKey.getBytes());
////
////        EncryptionDetails encryptionDetails = new EncryptionDetails(secretKey,
////                note.getEncryptionDetails().getVector(), note.getEncryptionDetails().getMethod());
////
////        MessageEncrypterImpl messageDecoder = getEncoderFor(encryptionDetails);
////
////        byte[] content = decodeBase64(note.getContent());
////
////        return messageDecoder.decrypt(content);
////    }
//
//    private void verifyKey(byte[] customKey) {
//        if(!Base64.isBase64(customKey))
//            throw new MalformedPrivateKeyException("Provided key is malformed.");
//    }
//}
